import { Pipe, PipeTransform } from '@angular/core';
import { UserHelpers } from '../../core/utils/user-helpers';

// Pipe to get full name from user object ,Usage: {{ user | fullName }}
@Pipe({
  name: 'fullName',
  standalone: true
})
export class FullNamePipe implements PipeTransform {
  transform(user: { firstName: string; lastName: string } | null | undefined): string {
    if (!user) return '';
    return UserHelpers.getFullName(user);
  }
}
