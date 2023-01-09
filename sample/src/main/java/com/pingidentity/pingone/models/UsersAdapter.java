package com.pingidentity.pingone.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pingidentity.pingone.R;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends ArrayAdapter<UserModel> {

    private final Context mContext;
    private final List<UserModel> dataSet;

    public UsersAdapter(@NonNull Context context, ArrayList<UserModel> usersList) {
        super(context, 0, usersList);
        this.mContext = context;
        this.dataSet = usersList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate
                    (R.layout.layout_user_row, parent, false);
        }

        UserModel user = dataSet.get(position);

        TextView userIdView = view.findViewById(R.id.userId);
        userIdView.setText(user.getUserId());

        TextView userEmail = view.findViewById(R.id.userEmail);
        userEmail.setText(user.getEmail());

        TextView given = view.findViewById(R.id.userGivenName);
        given.setText(user.getGiven());

        TextView family = view.findViewById(R.id.userFamilyName);
        family.setText(user.getFamily());

        TextView usernameView = view.findViewById(R.id.username);
        usernameView.setText(user.getUsername());
        return view;
    }
}

