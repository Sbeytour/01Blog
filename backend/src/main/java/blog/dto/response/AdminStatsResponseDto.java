package blog.dto.response;

public class AdminStatsResponseDto {

    private long totalUsers;
    private long totalPosts;
    private long pendingReports;
    private UserResponseDto mostReportedUser;
    private long mostReportedUserReportCount;

    public AdminStatsResponseDto() {
    }

    public AdminStatsResponseDto(long totalUsers, long totalPosts, long pendingReports,
                                  UserResponseDto mostReportedUser, long mostReportedUserReportCount) {
        this.totalUsers = totalUsers;
        this.totalPosts = totalPosts;
        this.pendingReports = pendingReports;
        this.mostReportedUser = mostReportedUser;
        this.mostReportedUserReportCount = mostReportedUserReportCount;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalPosts() {
        return totalPosts;
    }

    public void setTotalPosts(long totalPosts) {
        this.totalPosts = totalPosts;
    }

    public long getPendingReports() {
        return pendingReports;
    }

    public void setPendingReports(long pendingReports) {
        this.pendingReports = pendingReports;
    }

    public UserResponseDto getMostReportedUser() {
        return mostReportedUser;
    }

    public void setMostReportedUser(UserResponseDto mostReportedUser) {
        this.mostReportedUser = mostReportedUser;
    }

    public long getMostReportedUserReportCount() {
        return mostReportedUserReportCount;
    }

    public void setMostReportedUserReportCount(long mostReportedUserReportCount) {
        this.mostReportedUserReportCount = mostReportedUserReportCount;
    }
}
