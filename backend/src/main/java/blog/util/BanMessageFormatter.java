package blog.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BanMessageFormatter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a");
    
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
