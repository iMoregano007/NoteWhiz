package com.imoregano007.notewhiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetailsActivity extends AppCompatActivity {

    EditText titleEditText,contentEditText;
    ImageButton saveNoteBtn;
    TextView pageTitleTextV;
    Note note;
    Button deleteNote_btn;
    String title,content,docId;
    boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        titleEditText = findViewById(R.id.notes_titleText);
        contentEditText = findViewById(R.id.notes_contentText);
        saveNoteBtn = findViewById(R.id.save_noteBtn);
        pageTitleTextV = findViewById(R.id.page_title);
        deleteNote_btn = findViewById(R.id.deleteNote_btn);
//        receive data on note clicked



        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");
        if(docId != null && !docId.isEmpty()){
            isEditMode = true;
        }
        if(isEditMode){
            deleteNote_btn.setVisibility(View.VISIBLE);
            titleEditText.setText(title);
            contentEditText.setText(content);
            pageTitleTextV.setText("View/Edit your note");
        }

        saveNoteBtn.setOnClickListener(v -> saveNote());

        deleteNote_btn.setOnClickListener(v -> deleteNoteFromFirebase());

    }

    void saveNote() {
        String noteTitle = titleEditText.getText().toString();
        String contentText = contentEditText.getText().toString();
        if(noteTitle == null || noteTitle.isEmpty()){
            titleEditText.setError("Title is required");
            return;
        }

        note = new Note();
        note.setTitle(noteTitle);
        note.setContent(contentText);
        note.setTimestamp(Timestamp.now());

        saveNoteToFirebase(note);
    }

    void saveNoteToFirebase(Note note){

        DocumentReference documentReference;
        if(isEditMode){
//            update existing note
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        }else{
//            create new note
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }

        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    // note is added
                    Utility.showToast(NoteDetailsActivity.this,"Note added successfully");
                    finish();

                } else {
                    // note is not added due to any reason
                    Utility.showToast(NoteDetailsActivity.this,"Failed to add note");

                }

            }
        });


    }

    void deleteNoteFromFirebase(){

        DocumentReference documentReference;

//            delete note
        documentReference = Utility.getCollectionReferenceForNotes().document(docId);


        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    // note is deleted
                    Utility.showToast(NoteDetailsActivity.this,"Note deleted successfully");
                    finish();

                } else {
                    // note is not deleted due to any reason
                    Utility.showToast(NoteDetailsActivity.this,"Failed to delete note");

                }

            }
        });
    }

}