import { AbstractControl, FormGroup } from '@angular/forms';

/**
 * Utility class for handling form validation error messages
 * Eliminates duplicated getFieldErrorMsg methods across components
 */
export class FormValidationHelper {
  /**
   * Gets a user-friendly error message for a form field
   * @param field The form control to check
   * @param fieldName The display name of the field
   * @returns Error message or undefined if no error
   */
  static getFieldErrorMessage(field: AbstractControl | null, fieldName: string): string | undefined {
    if (!field || !field.errors || field.untouched) {
      return undefined;
    }

    const errors = field.errors;

    if (errors['required']) {
      return `${fieldName} is required`;
    }

    if (errors['email']) {
      return 'Please enter a valid email address';
    }

    if (errors['minlength']) {
      const requiredLength = errors['minlength'].requiredLength;
      return `${fieldName} must be at least ${requiredLength} characters`;
    }

    if (errors['maxlength']) {
      const requiredLength = errors['maxlength'].requiredLength;
      return `${fieldName} cannot exceed ${requiredLength} characters`;
    }

    if (errors['pattern']) {
      return `${fieldName} format is invalid`;
    }

    if (errors['min']) {
      return `${fieldName} must be at least ${errors['min'].min}`;
    }

    if (errors['max']) {
      return `${fieldName} cannot exceed ${errors['max'].max}`;
    }

    return 'Invalid input';
  }

  /**
   * Marks all controls in a form group as touched
   * Useful for showing validation errors when form is submitted
   */
  static markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();

      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  /**
   * Checks if a form field has errors and is touched
   */
  static hasError(field: AbstractControl | null): boolean {
    return !!(field && field.errors && field.touched);
  }

  /**
   * Checks if a specific error exists on a field
   */
  static hasSpecificError(field: AbstractControl | null, errorName: string): boolean {
    return !!(field && field.errors && field.errors[errorName] && field.touched);
  }
}
