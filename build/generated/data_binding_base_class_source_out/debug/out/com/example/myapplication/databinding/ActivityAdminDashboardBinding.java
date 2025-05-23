// Generated by view binder compiler. Do not edit!
package com.example.myapplication.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.myapplication.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityAdminDashboardBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button btnCheckRegistrations;

  @NonNull
  public final Button btnPutMenu;

  @NonNull
  public final Button btnRemoveRegistrations;

  private ActivityAdminDashboardBinding(@NonNull LinearLayout rootView,
      @NonNull Button btnCheckRegistrations, @NonNull Button btnPutMenu,
      @NonNull Button btnRemoveRegistrations) {
    this.rootView = rootView;
    this.btnCheckRegistrations = btnCheckRegistrations;
    this.btnPutMenu = btnPutMenu;
    this.btnRemoveRegistrations = btnRemoveRegistrations;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityAdminDashboardBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityAdminDashboardBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_admin_dashboard, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityAdminDashboardBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btnCheckRegistrations;
      Button btnCheckRegistrations = ViewBindings.findChildViewById(rootView, id);
      if (btnCheckRegistrations == null) {
        break missingId;
      }

      id = R.id.btnPutMenu;
      Button btnPutMenu = ViewBindings.findChildViewById(rootView, id);
      if (btnPutMenu == null) {
        break missingId;
      }

      id = R.id.btnRemoveRegistrations;
      Button btnRemoveRegistrations = ViewBindings.findChildViewById(rootView, id);
      if (btnRemoveRegistrations == null) {
        break missingId;
      }

      return new ActivityAdminDashboardBinding((LinearLayout) rootView, btnCheckRegistrations,
          btnPutMenu, btnRemoveRegistrations);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
