import { Pipe, PipeTransform } from '@angular/core';
import { DateFormatter } from '../../core/utils/date-formatter';

/**
 * Pipe to format dates as relative time
 * Usage: {{ dateString | relativeTime }}
 */
@Pipe({
  name: 'relativeTime',
  standalone: true
})
export class RelativeTimePipe implements PipeTransform {
  transform(value: string | undefined): string {
    if (!value) return '';
    return DateFormatter.formatRelativeTime(value);
  }
}
