import { Component, inject } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatDialogModule, MatDialogRef } from "@angular/material/dialog";

@Component({
    selector: 'app-dialog',
    templateUrl: './dialog.html',
    styleUrl: './dialog.scss',
    imports: [
        MatDialogModule,
    MatButtonModule,

    ]
})
export class DialogComponent {
    private dialogRef = inject(MatDialogRef<DialogComponent>);
    close(choice: boolean) {
        this.dialogRef.close(choice);
    }
}