import { HttpErrorResponse } from '@angular/common/http';
import { ErrorConfig, ErrorType } from '../../components/error-state/error-state';

export class ErrorHandler {
  /**
   * Maps HTTP error responses to ErrorConfig for consistent error display
   */
  static mapHttpError(error: HttpErrorResponse, entityName?: string): ErrorConfig {
    const type = this.getErrorType(error.status);

    return {
      type,
      entityName,
      showHomeButton: true,
      showBackButton: true,
      showRetryButton: type === 'network-error' || type === 'server-error'
    };
  }

  /**
   * Maps HTTP status codes to error types
   */
  private static getErrorType(status: number): ErrorType {
    switch (status) {
      case 404:
        return 'not-found';
      case 403:
        return 'forbidden';
      case 500:
      case 502:
      case 503:
      case 504:
        return 'server-error';
      case 0: // Network error
        return 'network-error';
      default:
        return 'generic';
    }
  }

  /**
   * Gets a user-friendly error message from HTTP error
   */
  static getErrorMessage(error: HttpErrorResponse): string {
    // Always prefer backend message if available
    if (error.error?.message) {
      return error.error.message;
    }

    if (error.status === 404) {
      return 'The requested resource was not found.';
    } else if (error.status === 403) {
      return 'You do not have permission to access this resource.';
    } else if (error.status === 401) {
      return 'Invalid credentials. Please check your username/email and password.';
    } else if (error.status === 0) {
      return 'Network error. Please check your connection.';
    } else if (error.status >= 500) {
      return 'Server error. Please try again later.';
    } else {
      return 'An unexpected error occurred. Please try again.';
    }
  }

  /**
   * Creates a banned user error config
   */
  static userBanned(message?: string): ErrorConfig {
    return {
      type: 'forbidden',
      title: 'Account Banned',
      message: message || 'Your account has been banned. Please contact support for more information.',
      icon: 'block',
      showHomeButton: false,
      showBackButton: false,
      showRetryButton: false
    };
  }

  /**
   * Creates a user not found error config
   */
  static userNotFound(username: string): ErrorConfig {
    return {
      type: 'not-found',
      title: 'User Not Found',
      message: `The user @${username} does not exist or may have been deleted.`,
      entityName: `@${username}`,
      showHomeButton: true,
      showBackButton: true,
      showRetryButton: false
    };
  }

  /**
   * Creates a post not found error config
   */
  static postNotFound(postId?: number): ErrorConfig {
    return {
      type: 'not-found',
      title: 'Post Not Found',
      message: 'This post does not exist or may have been deleted.',
      entityName: postId ? `Post #${postId}` : undefined,
      showHomeButton: true,
      showBackButton: true,
      showRetryButton: false
    };
  }

  /**
   * Creates a forbidden access error config
   */
  static forbidden(resource: string = 'resource'): ErrorConfig {
    return {
      type: 'forbidden',
      message: `You don't have permission to access this ${resource}.`,
      showHomeButton: true,
      showBackButton: true,
      showRetryButton: false
    };
  }

  /**
   * Creates a network error config
   */
  static networkError(): ErrorConfig {
    return {
      type: 'network-error',
      showHomeButton: true,
      showBackButton: false,
      showRetryButton: true
    };
  }
}
