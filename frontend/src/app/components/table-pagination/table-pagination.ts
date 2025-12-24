import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';

/**
 * Simple Table Pagination Component
 *
 * Usage:
 * <app-table-pagination
 *   [totalElements]="totalElements()"
 *   [pageSize]="pageSize()"
 *   [pageIndex]="pageIndex()"
 *   (pageChange)="onPageChange($event)">
 * </app-table-pagination>
 */
@Component({
  selector: 'app-table-pagination',
  standalone: true,
  imports: [CommonModule, MatPaginatorModule],
  templateUrl: './table-pagination.html',
  styleUrl: './table-pagination.scss'
})
export class TablePagination {
  @Input() totalElements = 0;
  @Input() pageSize = 20;
  @Input() pageIndex = 0;
  @Input() pageSizeOptions: number[] = [10, 20, 50, 100];
  @Input() showFirstLastButtons = true;

  @Output() pageChange = new EventEmitter<PageEvent>();

  /**
   * Handles page change event from Material Paginator
   * Emits the page event to parent component
   */
  onPageChange(event: PageEvent): void {
    this.pageChange.emit(event);
  }
}
