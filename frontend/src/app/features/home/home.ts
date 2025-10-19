import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Navbar } from '../../components/navbar/navbar';
import { PostService } from '../../core/services/postService';
import { Post } from '../../core/models/post';
import { PostCard } from '../../components/post-card/post-card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-home',
  imports: [
    Navbar,
    CommonModule,
    PostCard,
    MatProgressSpinnerModule,
    MatIconModule
  ],
  templateUrl: './home.html',
  styleUrl: './home.scss'
})
export class Home implements OnInit {
  private postService = inject(PostService);

  posts = signal<Post[]>([]);
  isLoading = signal(true);
  errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.loadAllPosts();
  }

  loadAllPosts(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.postService.getAllPosts().subscribe({
      next: (posts) => {
        this.posts.set(posts);
        this.isLoading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error loading posts:', error);
        this.errorMessage.set('Failed to load posts. Please try again later.');
        this.isLoading.set(false);
      }
    });
  }

  onRetry(): void {
    this.loadAllPosts();
  }
}