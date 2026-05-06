import React, { useEffect, useMemo, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import BorrowerNavbar from "../../../Components/Borrower/BorrowerNavbar/Navbar";
import Footer from "../../../Components/Shared/Footer/Footer";
import SimilarItemCard from "../../../Components/Borrower/ItemDetailCard/ItemDetailCard"
import Camera from "../../../assets/camera.jpg"
import "./ItemDetailPage.css";
import { getApiClient } from "../../../api/ApiClientSingleton";

import electronics from "../../../assets/electronics.png"
import exercise from "../../../assets/exercise.png"
import sports from "../../../assets/sports.png"
import technology from "../../../assets/technology.png"
import instruments from "../../../assets/instruments.png"


function statusLabel(status) {
  const s = String(status || "").toUpperCase();
  if (s === "AVAILABLE") return "AVAILABLE";
  if (s === "BORROWED") return "BORROWED";
  return "Maintenance";
}

const CATEGORY_ICONS = {
  electronics,
  exercise,
  sports,
  technology,
  instruments,
};

export default function ItemDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [item, setItem] = useState(null);
  const [similarItems, setSimilarItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // Borrow modal states
  const [showBorrowModal, setShowBorrowModal] = useState(false);
  const [borrowStep, setBorrowStep] = useState(1); // 1 = summary input, 2 = confirm
  const [assetTagInput, setAssetTagInput] = useState("");
  const [borrowSubmitting, setBorrowSubmitting] = useState(false);
  const [borrowMessage, setBorrowMessage] = useState("");

  const isAvailable = useMemo(
    () => String(item?.status || "").toUpperCase() === "AVAILABLE",
    [item]
  );

  useEffect(() => {
    let cancelled = false;

    async function load() {
      setLoading(true);
      setError("");
      try {
        const itemRes = await getApiClient().request(`/api/items/${id}`, {
          method: "GET",
          headers: { "Content-Type": "application/json" },
        });

        if (cancelled) return;
        const itemData = await itemRes.json();
        const detail = itemData.data;
        setItem(detail);

        // Fetch a page of items to simulate "Similar Items"
        const listRes = await getApiClient().request("/api/items?page=0&size=20", {
          method: "GET",
          headers: { "Content-Type": "application/json" },
        });

        if (cancelled) return;
        const listdata = await listRes.json();
        const content = listdata?.data?.content || [];
        const sameCategory = content.filter(
          (x) => x.itemId !== detail.itemId && x.categoryId === detail.categoryId
        );
        setSimilarItems(sameCategory.slice(0, 8));
      } catch (err) {
        if (cancelled) return;
        setError(err.message || "Failed to load item details.");
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    load();
    return () => {
      cancelled = true;
    };
  }, [id]);

  const openBorrowModal = () => {
    setBorrowStep(1);
    setBorrowMessage("");
    setAssetTagInput("");
    setShowBorrowModal(true);
  };

  const closeBorrowModal = () => {
    setShowBorrowModal(false);
    setBorrowStep(1);
    setBorrowSubmitting(false);
  };

  const handleContinueToConfirm = () => {
    if (!assetTagInput.trim()) {
      setBorrowMessage("Please enter the asset tag first.");
      return;
    }
    setBorrowMessage("");
    setBorrowStep(2);
  };

  const handleConfirmBorrow = async () => {
    if (!item) return;
    setBorrowSubmitting(true);
    setBorrowMessage("");
    try {
      await getApiClient().request("/api/transactions/borrow", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          itemId: item.itemId,
          assetTag: assetTagInput.trim(),
        }),
      });

      setBorrowMessage("Borrow successful!");
      setShowBorrowModal(false);

      // Refresh item status
      const refreshed = await getApiClient().request(`/api/items/${item.itemId}`, {
        method: "GET",
        headers: { "Content-Type": "application/json" },
      });

      const refreshedData = await refreshed.json();
      setItem(refreshedData.data);
    } catch (err) {
      setBorrowMessage(err.message || "Borrow failed.");
    } finally {
      setBorrowSubmitting(false);
    }
  };


  function normalizeCategoryKey(categoryName) {
      // handles "Electronics", "electornics", "electronic items", etc.
      const raw = String(categoryName || "").toLowerCase().trim();
      if (raw.includes("electornic")) return "electronics"; // typo-safe
      if (raw.includes("electronic")) return "electronics";
      if (raw.includes("exercise") || raw.includes("fitness") || raw.includes("gym")) return "exercise";
      if (raw.includes("sport")) return "sports";
      if (raw.includes("instrument")) return "instruments";
      if (raw.includes("tech") || raw.includes("gadget")) return "technology";
      return "technology"; // fallback
    }
    function getCategoryIcon(categoryName) {
      const key = normalizeCategoryKey(categoryName);
      return CATEGORY_ICONS[key] || technology;
    }

  if (loading) {
    return (
      <div className="detail-page">
        <BorrowerNavbar />
        <main className="detail-main"><div className="detail-alert">Loading item details...</div></main>
        <Footer />
      </div>
    );
  }

  if (error || !item) {
    return (
      <div className="detail-page">
        <BorrowerNavbar />
        <main className="detail-main">
          <div className="detail-alert detail-error">{error || "Item not found."}</div>
        </main>
        <Footer />
      </div>
    );
  }

  const normalizedStatus = String(item.status || "").toLowerCase();

  return (
    <div className="detail-page">
      <BorrowerNavbar />

      <main className="detail-main">
        <div className="breadcrumb">
          <Link to="/borrower">Home</Link>
          <span>&gt;</span>
          <span>{item.categoryName || "Category"}</span>
          <span>&gt;</span>
          <strong>{item.name}</strong>
        </div>

        <h1 className="detail-title">Item Details</h1>

        <section className="detail-top">
          <div className="detail-image-wrap">
            <img
              src={item.imageFileUrl}
              alt={item.name}
              className="detail-image"
            />
          </div>

          <aside className="detail-panel">
            <h2>{item.name}</h2>
            
            <p className="detail-category"> 
              <img
                src={getCategoryIcon(item.categoryName)}
                alt={item.categoryName || "Category"}
                className="inline-icon"
              />
              <span>{item.categoryName || "Uncategorized"}</span>
              </p>

            <div className={`details-status ${normalizedStatus}`}>
              ● {statusLabel(item.status)}
            </div>

            <hr />

            <h3>Description</h3>
            <p className="detail-description">
              {item.description || "No description available for this item."}
            </p>

            <button
              className="borrow-btn"
              onClick={openBorrowModal}
              disabled={!isAvailable}
              type="button"
            >
              {isAvailable ? "Confirm Borrow" : "Item Unavailable"}
            </button>
          </aside>
        </section>

        <section className="similar-section">
          <div className="similar-header">
            <h3>Similar Items</h3>
            <button
              type="button"
              className="see-all-link"
              onClick={() => navigate("/borrower")}
            >
              See All &gt;
            </button>
          </div>

            <div className="similar-grid">
                {similarItems.length > 0 ? (
                    similarItems.map((s) => <SimilarItemCard key={s.itemId} item={s} />)
                ) : (
                    <div className="detail-alert">No similar items found.</div>
                )}
            </div>
        </section>
      </main>

      <Footer />

      {showBorrowModal && (
        <div className="modal-overlay" onClick={closeBorrowModal}>
          <div className="borrow-modal" onClick={(e) => e.stopPropagation()}>
            {borrowStep === 1 && (
              <>
                <h3>Borrow Summary</h3>
                <p><strong>Item:</strong> {item.name}</p>
                <p><strong>Category:</strong> {item.categoryName || "Uncategorized"}</p>
                <p><strong>Borrow Time:</strong> {new Date().toLocaleString()}</p>

                <label htmlFor="assetTagInput">Asset Tag</label>
                <input
                  id="assetTagInput"
                  type="text"
                  value={assetTagInput}
                  onChange={(e) => setAssetTagInput(e.target.value)}
                  placeholder="Enter item asset tag (e.g. DSLR-001)"
                />

                {borrowMessage && <div className="detail-alert detail-error">{borrowMessage}</div>}

                <div className="modal-actions">
                  <button type="button" onClick={closeBorrowModal} className="ghost-btn">Cancel</button>
                  <button type="button" onClick={handleContinueToConfirm} className="primary-btn">Continue</button>
                </div>
              </>
            )}

            {borrowStep === 2 && (
              <>
                <h3>Confirm Borrow</h3>
                <p>Please confirm this borrow request:</p>
                <p><strong>Item:</strong> {item.name}</p>
                <p><strong>Asset Tag:</strong> {assetTagInput}</p>

                {borrowMessage && <div className="detail-alert detail-error">{borrowMessage}</div>}

                <div className="modal-actions">
                  <button type="button" onClick={() => setBorrowStep(1)} className="ghost-btn">Back</button>
                  <button
                    type="button"
                    onClick={handleConfirmBorrow}
                    className="primary-btn"
                    disabled={borrowSubmitting}
                  >
                    {borrowSubmitting ? "Submitting..." : "Confirm Borrow"}
                  </button>
                </div>
              </>
            )}
          </div>
        </div>
      )}
    </div>
  );
}