import { Component, ViewChild, ElementRef, AfterViewInit, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

/**
 * Simple Infinite Scroll Component
 *
 * Usage:
 * <app-infinite-scroll
 *   [isLoadingMore]="isLoadingMore()"
 *   [hasMore]="hasMore()"
 *   (loadMore)="loadMoreItems()">
 *   <!-- Your content here -->
 * </app-infinite-scroll>
 */
@Component({
  selector: 'app-infinite-scroll',
  standalone: true,
  imports: [CommonModule, MatProgressSpinnerModule],
  templateUrl: './infinite-scroll.html',
  styleUrl: './infinite-scroll.scss'
})
export class InfiniteScroll implements AfterViewInit, OnDestroy {
  @Input() isLoadingMore = false;
  @Input() hasMore = false;
  @Input() loadingMessage = 'Loading more...';
  @Input() noMoreMessage = 'No more items';

  @Output() loadMore = new EventEmitter<void>();

  @ViewChild('scrollSentinel') scrollSentinel?: ElementRef;

  private observer?: IntersectionObserver;

  ngAfterViewInit(): void {
    this.setupScrollObserver();
  }

  ngOnDestroy(): void {
    if (this.observer) {
      this.observer.disconnect();
    }
  }

  /**
   * Sets up an IntersectionObserver to watch when the sentinel element comes into view
   * When it does, and we have more items to load, emits the loadMore event
   */
  private setupScrollObserver(): void {
    // If sentinel not ready yet, try again in 100ms
    if (!this.scrollSentinel) {
      setTimeout(() => this.setupScrollObserver(), 100);
      return;
    }

    // Create observer that triggers when sentinel is visible
    this.observer = new IntersectionObserver(
      (entries) => {
        const entry = entries[0];
        // If sentinel is visible AND we have more items AND not currently loading
        if (entry.isIntersecting && this.hasMore && !this.isLoadingMore) {
          this.loadMore.emit();
        }
      },
      {
        root: null,           // Use viewport as root
        rootMargin: '200px',  // Trigger 200px before reaching the end
        threshold: 0.3        // Trigger as soon as any part is visible
      }
    );

    this.observer.observe(this.scrollSentinel.nativeElement);
  }
}
