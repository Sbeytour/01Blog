import { Pipe, PipeTransform } from '@angular/core';
import { StringHelpers } from '../../core/utils/string-helpers';

/**
 * Pipe to truncate strings to a specified length
 * Usage: {{ longText | truncate:50 }}
 */
@Pipe({
  name: 'truncate',
  standalone: true
})
export class TruncatePipe implements PipeTransform {
  transform(value: string, maxLength: number = 50): string {
    if (!value) return '';
    return StringHelpers.truncate(value, maxLength);
  }
}
