import React, { useState } from "react";
import { getApiClient } from "../../../api/ApiClientSingleton";

const initialForm = {
  name: "",
  description: "",
  assetTag: "",
  categoryId: "",
  imageFile: null,
};

async function requestJson(path, options = {}) {
  const res = await getApiClient().request(path, options);

  let body = null;
  try {
    body = await res.json();
  } catch {
    body = null;
  }

  if (!res.ok || body?.success === false) {
    const message = body?.error?.message || body?.message || `Request failed (${res.status})`;
    throw new Error(message);
  }

  return body;
}

export default function AddItemModal({ isOpen, onClose, onCreated }) {
  const [form, setForm] = useState(initialForm);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  if (!isOpen) return null;

  const onFormChange = (e) => {
    const { name, value, files } = e.target;
    if (name === "imageFile") {
      setForm((prev) => ({ ...prev, imageFile: files?.[0] || null }));
      return;
    }
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleClose = () => {
    if (submitting) return;
    setForm(initialForm);
    setError("");
    onClose?.();
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSubmitting(true);

    try {
      if (!form.name.trim() || !form.assetTag.trim() || !String(form.categoryId).trim()) {
        throw new Error("Name, asset tag, and category are required.");
      }

      const fd = new FormData();
      fd.append("name", form.name.trim());
      fd.append("description", form.description || "");
      fd.append("assetTag", form.assetTag.trim());
      fd.append("categoryId", String(form.categoryId).trim());
      if (form.imageFile) fd.append("imageFile", form.imageFile);

      const body = await requestJson("/api/admin/items", { method: "POST", body: fd });

      handleClose();
      onCreated?.(body?.data);
    } catch (err) {
      setError(err.message || "Failed to add item.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="inv-modal-backdrop">
      <div className="inv-modal">
        <h2>Add New Item</h2>
        <form onSubmit={handleSubmit} className="inv-form">
          <label>Name<input name="name" value={form.name} onChange={onFormChange} required /></label>
          <label>Description<textarea name="description" value={form.description} onChange={onFormChange} rows={3} /></label>
          <label>Asset Tag<input name="assetTag" value={form.assetTag} onChange={onFormChange} required /></label>
          <label>Category ID<input name="categoryId" type="number" value={form.categoryId} onChange={onFormChange} required /></label>
          <label>Item Image<input name="imageFile" type="file" accept="image/png,image/jpeg,image/webp" onChange={onFormChange} /></label>

          {error && <div className="alert error">{error}</div>}

          <div className="inv-modal-actions">
            <button type="button" onClick={handleClose} disabled={submitting} className="cancel-btn">Cancel</button>
            <button type="submit" className="btn-add-item" disabled={submitting}>
              {submitting ? "Saving..." : "Add Item"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}