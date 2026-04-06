import React, { useEffect, useMemo, useState } from "react";
import BorrowerNavbar from "../../../Components/Borrower/BorrowerNavbar/Navbar";
import Footer from "../../../Components/Shared/Footer/Footer"
import ItemCard from "../../../Components/Borrower/ItemCategoriesCard/ItemCard";
import { apiFetch } from "../../../Utils/apiFetch";
import { getAuthHeader } from "../../../security/auth";
import "./ExploreItemsPage.css";

const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const CATEGORY_OPTIONS = [
  { id: "", label: "All" },
  { id: 1, label: "Electronics" },
  { id: 2, label: "Sports" },
  { id: 3, label: "Technology" },
  { id: 4, label: "Exercise" },
  { id: 5, label: "Instruments" },
];

const STATUS_OPTIONS = [
  { value: "", label: "All Status" },
  { value: "AVAILABLE", label: "Available" },
  { value: "BORROWED", label: "Borrowed" },
  { value: "MAINTENANCE", label: "Maintenance" },
];

export default function ExploreItemsPage() {
  const [items, setItems] = useState([]);
  const [query, setQuery] = useState("");
  const [categoryId, setCategoryId] = useState("");
  const [status, setStatus] = useState("");
  const [sortBy, setSortBy] = useState("Relevant");
  const [page, setPage] = useState(0);
  const [size] = useState(9);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [pagination, setPagination] = useState({
    totalPages: 0,
    totalElements: 0,
    last: true,
  });

  const requestUrl = useMemo(() => {
    const params = new URLSearchParams();
    if (query.trim()) params.set("query", query.trim());
    if (categoryId) params.set("categoryId", categoryId);
    if (status) params.set("status", status);
    params.set("page", String(page));
    params.set("size", String(size));
    return `${API_BASE}/api/items?${params.toString()}`;
  }, [query, categoryId, status, page, size]);

  useEffect(() => {
    let cancelled = false;

    async function load() {
      setLoading(true);
      setError("");
      try {
        const body = await apiFetch(requestUrl, {
          method: "GET",
          headers: {
            ...getAuthHeader(),
          },
        });
        if (cancelled) return;
        const data = body.data || {};
        setItems(data.content || []);
        setPagination({
          totalPages: data.totalPages || 0,
          totalElements: data.totalElements || 0,
          last: data.last ?? true,
        });
      } catch (err) {
        if (cancelled) return;
        setItems([]);
        setError(err.message || "Failed to fetch items.");
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    

    load();
    return () => {
      cancelled = true;
    };
  }, [requestUrl]);

  const handleCategoryClick = (id) => {
    setCategoryId(String(id));
    setPage(0);
  };

  return (
    <div className="explore-page">
      <BorrowerNavbar />

      <section className="explore-hero">
        <h1 className="explore-title">
          Explore <span>Items</span>
        </h1>

        <div className="explore-category-chips">
          {CATEGORY_OPTIONS.map((c) => {
            const active = String(c.id) === String(categoryId);
            return (
              <button
                key={String(c.id)}
                type="button"
                className={`chip ${active ? "active" : ""}`}
                onClick={() => handleCategoryClick(c.id)}
              >
                {c.label}
              </button>
            );
          })}
        </div>
      </section>

      <main className="explore-content">
        <div className="explore-toolbar">
          <button type="button" className="filter-btn">
            &#x25BC; Filter
          </button>

          <input
            className="search-input"
            placeholder="Search items"
            value={query}
            onChange={(e) => {
              setQuery(e.target.value);
              setPage(0);
            }}
          />

          <div className="toolbar-right">
            <label htmlFor="status-select">Status:</label>
            <select
              id="status-select"
              value={status}
              onChange={(e) => {
                setStatus(e.target.value);
                setPage(0);
              }}
            >
              {STATUS_OPTIONS.map((s) => (
                <option key={s.value} value={s.value}>
                  {s.label}
                </option>
              ))}
            </select>

            <label htmlFor="sort-select">Sort by:</label>
            <select
              id="sort-select"
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value)}
            >
              <option>Relevant</option>
              <option>Newest</option>
              <option>Name A-Z</option>
            </select>

            <button type="button" className="icon-btn" title="Grid View">
              &#9638;
            </button>
            <button type="button" className="icon-btn" title="List View">
              &#9776;
            </button>
          </div>
        </div>

        {error ? <div className="alert error">{error}</div> : null}
        {loading ? <div className="alert">Loading items...</div> : null}

        {!loading && !error && (
          <>
            <div className="item-grid">
              {items.length > 0 ? (
                items.map((item) => <ItemCard key={item.itemId} item={item} />)
              ) : (
                <div className="alert">No items found matching your filters.</div>
              )}
            </div>

            {pagination.totalPages > 1 && (
              <div className="pagination">
                <button
                  type="button"
                  className="page-btn"
                  disabled={page === 0}
                  onClick={() => setPage((p) => p - 1)}
                >
                  &lt;
                </button>

                {Array.from({ length: pagination.totalPages }).map((_, index) => (
                  <button
                    key={index}
                    type="button"
                    className={`page-btn ${page === index ? "active" : ""}`}
                    onClick={() => setPage(index)}
                  >
                    {index + 1}
                  </button>
                ))}

                <button
                  type="button"
                  className="page-btn"
                  disabled={pagination.last}
                  onClick={() => setPage((p) => p + 1)}
                >
                  &gt;
                </button>
              </div>
            )}
          </>
        )}
      </main>

      <Footer />
    </div>
  );
}