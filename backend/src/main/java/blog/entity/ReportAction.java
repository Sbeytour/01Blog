package blog.entity;

public enum ReportAction {
    NONE,           // Just resolve the report without taking action
    BAN_USER,       // Ban the reported user
    DELETE_POST,    // Delete the reported post
    DELETE_USER     // Permanently delete the reported user
}
