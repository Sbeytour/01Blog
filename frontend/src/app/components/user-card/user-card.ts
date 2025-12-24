import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { User } from '../../core/models/user';

@Component({
  selector: 'app-user-card',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  templateUrl: './user-card.html',
  styleUrl: './user-card.scss'
})
export class UserCard {
  @Input() user!: User;
  @Output() userClick = new EventEmitter<User>();

  onUserClick() {
    this.userClick.emit(this.user);
  }
}
