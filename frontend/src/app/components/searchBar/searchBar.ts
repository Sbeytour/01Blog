import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { UserService } from '../../core/services/userService';
import { User } from '../../core/models/user';
import { UserCard } from '../user-card/user-card';
import { EmptyState } from '../empty-state/empty-state';

@Component({
  selector: 'app-searchBar',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    UserCard,
    EmptyState
  ],
  templateUrl: './searchBar.html',
  styleUrl: './searchBar.scss',
})
export class SearchBarComponent {
  private userService = inject(UserService);
  private router = inject(Router);

  searchQuery = '';
  searchResults = signal<User[]>([]);
  isSearching = signal(false);
  showSearchResults = signal(false);

  private searchSubject = new Subject<string>();

  constructor() {
    this.searchSubject
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap((query) => {
          if (query.trim().length === 0) {
            this.searchResults.set([]);
            this.isSearching.set(false);
            this.showSearchResults.set(false);
            return [];
          }
          this.isSearching.set(true);
          this.showSearchResults.set(true);
          return this.userService.searchUsers(query);
        })
      )
      .subscribe({
        next: (users) => {
          this.searchResults.set(users);
          this.isSearching.set(false);
        },
        error: () => {
          this.searchResults.set([]);
          this.isSearching.set(false);
        },
      });
  }

  onSearchChange() {
    this.searchSubject.next(this.searchQuery);
  }

  onSearchFocus() {
    if (this.searchQuery.trim().length > 0) {
      this.showSearchResults.set(true);
    }
  }

  clearSearch() {
    this.searchQuery = '';
    this.searchResults.set([]);
    this.showSearchResults.set(false);
  }

  onUserClick(user: User) {
    this.router.navigate(['/profile', user.username]);
    this.clearSearch();
  }

  closeSearchResults() {
    setTimeout(() => {
      this.showSearchResults.set(false);
    }, 200);
  }
}
