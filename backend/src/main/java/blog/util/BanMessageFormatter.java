package blog.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for formatting ban-related messages consistently across the application.
 */
public class BanMessageFormatter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a");

    private BanMessageFormatter() {
        // Private constructor to prevent instantiation
    }

    /**
     * Generates a formatted ban message for a user.
     *
     * @param bannedUntil The date/time when the ban expires, or null for permanent ban
     * @param banReason   The reason for the ban
     * @return Formatted ban message
     */
    public static String formatBanMessage(LocalDateTime bannedUntil, String banReason) {
        StringBuilder banMessage = new StringBuilder("Your account has been banned");

        if (bannedUntil != null) {
            String formattedDate = bannedUntil.format(DATE_FORMATTER);
            banMessage.append(" until ").append(formattedDate);
        } else {
            banMessage.append(" permanently");
        }

        if (banReason != null && !banReason.isEmpty()) {
            banMessage.append(". Reason: ").append(banReason);
        }

        return banMessage.toString();
    }
}
