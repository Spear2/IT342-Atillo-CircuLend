import React from "react";

export default function AuthForm({ title, subtitle, onSubmit, children, className = "form-container" }) {
  return (
    <form className={className} onSubmit={onSubmit}>
      {title ? <h2 className="form-title">{title}</h2> : null}
      {subtitle ? <p className="form-subtitle">{subtitle}</p> : null}
      {children}
    </form>
  );
}
