// Generated by view binder compiler. Do not edit!
package com.example.myapplication.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.myapplication.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class AnnoncementBinding implements ViewBinding {
  @NonNull
  private final ScrollView rootView;

  @NonNull
  public final Button btnAddAnnouncement;

  @NonNull
  public final EditText etMessage;

  @NonNull
  public final EditText etTitle;

  @NonNull
  public final RecyclerView recyclerAdminAnnouncements;

  private AnnoncementBinding(@NonNull ScrollView rootView, @NonNull Button btnAddAnnouncement,
      @NonNull EditText etMessage, @NonNull EditText etTitle,
      @NonNull RecyclerView recyclerAdminAnnouncements) {
    this.rootView = rootView;
    this.btnAddAnnouncement = btnAddAnnouncement;
    this.etMessage = etMessage;
    this.etTitle = etTitle;
    this.recyclerAdminAnnouncements = recyclerAdminAnnouncements;
  }

  @Override
  @NonNull
  public ScrollView getRoot() {
    return rootView;
  }

  @NonNull
  public static AnnoncementBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static AnnoncementBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.annoncement, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static AnnoncementBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btnAddAnnouncement;
      Button btnAddAnnouncement = ViewBindings.findChildViewById(rootView, id);
      if (btnAddAnnouncement == null) {
        break missingId;
      }

      id = R.id.etMessage;
      EditText etMessage = ViewBindings.findChildViewById(rootView, id);
      if (etMessage == null) {
        break missingId;
      }

      id = R.id.etTitle;
      EditText etTitle = ViewBindings.findChildViewById(rootView, id);
      if (etTitle == null) {
        break missingId;
      }

      id = R.id.recyclerAdminAnnouncements;
      RecyclerView recyclerAdminAnnouncements = ViewBindings.findChildViewById(rootView, id);
      if (recyclerAdminAnnouncements == null) {
        break missingId;
      }

      return new AnnoncementBinding((ScrollView) rootView, btnAddAnnouncement, etMessage, etTitle,
          recyclerAdminAnnouncements);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
