import { AbstractControl, FormGroup } from '@angular/forms';

export class FormValidationHelper {
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

  static markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();

      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  static hasError(field: AbstractControl | null): boolean {
    return !!(field && field.errors && field.touched);
  }

  static hasSpecificError(field: AbstractControl | null, errorName: string): boolean {
    return !!(field && field.errors && field.errors[errorName] && field.touched);
  }
}
