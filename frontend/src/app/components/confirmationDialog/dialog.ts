import { Component, inject } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from "@angular/material/dialog";

export interface dialogData {
    title?: string,
    message?: string
}
@Component({
    selector: 'app-dialog',
    templateUrl: './dialog.html',
    imports: [
        MatDialogModule,
        MatButtonModule,

    ]
})
export class DialogComponent {
   dialogRef = inject(MatDialogRef<DialogComponent>);
   dialogData = inject<dialogData>(MAT_DIALOG_DATA);

    close(choice: boolean) {
        this.dialogRef.close(choice);
    }
}