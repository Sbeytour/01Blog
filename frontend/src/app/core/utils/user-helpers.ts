export class UserHelpers {
  static getFullName(user: { firstName: string; lastName: string }): string {
    return `${user.firstName} ${user.lastName}`.trim();
  }
}
