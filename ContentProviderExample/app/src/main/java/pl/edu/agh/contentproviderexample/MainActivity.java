package pl.edu.agh.contentproviderexample;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends ListActivity {
    private static final int DIALOG_EDIT = 1;
    private static final int DIALOG_DELETE = 2;
    private static final int DIALOG_CREATE = 3;

    private Button btnAddNewContact;
    private View editDialogView;
    private View addDialogView;
    private EditText dialogEtFirstNameEdit;
    private EditText dialogEtLastNameEdit;
    private EditText dialogEtFirstNameAdd;
    private EditText dialogEtLastNameAdd;

    private ContentResolver contentResolver;
    private Cursor contactsCursor;
    private ContactListAdapter adapter;
    private int currentContactPosition = 0;

    private OnClickListener onItemDeleteClickListener = new OnClickListener() {
        public void onClick(View v) {
            currentContactPosition = (Integer) v.getTag();
            showDialog(DIALOG_DELETE);
        }
    };

    private OnClickListener onItemEditClickListener = new OnClickListener() {
        public void onClick(View v) {
            currentContactPosition = (Integer) v.getTag();
            contactsCursor.moveToPosition(currentContactPosition);
            dialogEtFirstNameEdit.setText(getFirstNameOfCurrentContact());
            dialogEtLastNameEdit.setText(getLastNameOfCurrentContact());
            showDialog(DIALOG_EDIT);
        }
    };

    private String getFirstNameOfCurrentContact() {
        String displayName = contactsCursor.getString(1);
        String[] tokens = displayName.split(" ");
        if(tokens != null && tokens.length > 0) {
            return tokens[0];
        }
        return "";
    }

    private String getLastNameOfCurrentContact() {
        String displayName = contactsCursor.getString(1);
        String[] tokens = displayName.split(" ");
        if(tokens != null && tokens.length > 1) {
            return tokens[tokens.length - 1];
        }
        return "";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAddNewContact = (Button) findViewById(R.id.btnAddNewContact);
        btnAddNewContact.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dialogEtFirstNameEdit.setText("");
                dialogEtLastNameEdit.setText("");
                showDialog(DIALOG_CREATE);
            }
        });
        initDialogViews();
        initDialogFields();
        contentResolver = getContentResolver();
        contactsCursor = getContactsCursor();
        startManagingCursor(contactsCursor);
        fillListView();
    }

    private void initDialogViews() {
        LayoutInflater inflater = this.getLayoutInflater();
        addDialogView = inflater.inflate(R.layout.new_contact_dialog,
                (ViewGroup) findViewById(R.id.layout_root));
        editDialogView = inflater.inflate(R.layout.edit_contact_dialog,
                (ViewGroup) findViewById(R.id.layout_root));
    }

    private void initDialogFields() {
        dialogEtFirstNameEdit = (EditText) editDialogView.findViewById(R.id.etFirstNameEdit);
        dialogEtLastNameEdit = (EditText) editDialogView.findViewById(R.id.etLastNameEdit);
        dialogEtFirstNameAdd = (EditText) addDialogView.findViewById(R.id.etFirstNameAdd);
        dialogEtLastNameAdd = (EditText) addDialogView.findViewById(R.id.etLastNameAdd);
    }

    private Cursor getContactsCursor() {
        Uri uri = RawContacts.CONTENT_URI;
        String[] projection = { RawContacts._ID, Contacts.DISPLAY_NAME };
        String selection = RawContacts.DELETED + "=?";
        String[] selectionArgs = { Integer.toString(0) };
        String sortOrder = Contacts.DISPLAY_NAME + " ASC";
        return contentResolver.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    private void fillListView() {
        adapter = new ContactListAdapter(getApplicationContext(), R.layout.contact_row,
                contactsCursor,	new String[0], new int[0]);
        adapter.setOnItemDeleteClickListener(onItemDeleteClickListener);
        adapter.setOnItemEditClickListener(onItemEditClickListener);
        setListAdapter(adapter);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_EDIT:
                return getEditDialog();
            case DIALOG_DELETE:
                return getDeleteDialog();
            case DIALOG_CREATE:
                return getCreateDialog();
            default:
                return null;
        }
    }

    private Dialog getEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        contactsCursor.moveToPosition(currentContactPosition);
        builder.setTitle("Edit contact");
        builder.setView(editDialogView);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                updateCurrentContact();
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", null);
        return builder.create();
    }

    private Dialog getDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure?");
        builder.setMessage("Do you really want to delete this contact?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteCurrentContact();
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("No", null);
        return builder.create();
    }

    private Dialog getCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create new contact");
        builder.setView(addDialogView);
        dialogEtFirstNameAdd.setText("");
        dialogEtLastNameAdd.setText("");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                addNewContact();
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", null);
        return builder.create();
    }

    private void addNewContact() {
        String firstName = dialogEtFirstNameAdd.getText().toString();
        String lastName = dialogEtLastNameAdd.getText().toString();
        //TODO
    }

    private void updateCurrentContact() {
        contactsCursor.moveToPosition(currentContactPosition);
        String firstName = dialogEtFirstNameEdit.getText().toString();
        String lastName = dialogEtLastNameEdit.getText().toString();
        //TODO
    }

    private void deleteCurrentContact() {
        contactsCursor.moveToPosition(currentContactPosition);
        //TODO
    }
}
