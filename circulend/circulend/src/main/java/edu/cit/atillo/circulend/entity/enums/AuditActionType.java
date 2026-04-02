package edu.cit.atillo.circulend.entity.enums;

public enum AuditActionType {
    ITEM_ADDED,
    ITEM_UPDATED,
    ITEM_DELETED,
    BORROW_CONFIRMED,
    RETURN_CONFIRMED,
    SMTP_SENT,
    SMTP_FAILED,
    AUTH_LOGIN_SUCCESS,
    AUTH_LOGIN_FAILED,
    AUTH_EMAIL_VERIFIED
}