package com.balabka.todo;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;

import java.io.File;
import java.util.List;

public class ListAdapter extends BaseAdapter {

    private static final String TAG = "ListAdapter";
    private static final StrikethroughSpan STRIKE_THROUGH_SPAN = new StrikethroughSpan();

    private TaskEntity task;
    private Context context;

    public ListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return new Select().from(TaskEntity.class).count();
    }

    @Override
    public TaskEntity getItem(int i) {
        return new Select().from(TaskEntity.class).offset(i).limit(1).executeSingle();
    }

    @Override
    public long getItemId(int i) {
        return new Select().from(TaskEntity.class).offset(i).limit(1).executeSingle().getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        task = getItem(position);
        if (task == null) {
            Log.e(TAG, "NO RECORD FOUND");
            this.notifyDataSetChanged();
            return null;
        }

        vh.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openClickHandle(task);
            }
        });

        // checkbox
        vh.checkBox.setChecked(task.isDone);
        vh.checkBox.setTag(task.getId());
        vh.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkboxClickHandle(view);
            }
        });

        // text
        if (task.isDone) {
            vh.textView.setText(task.text, TextView.BufferType.SPANNABLE);
            Spannable spannable = (Spannable) vh.textView.getText();
            spannable.setSpan(STRIKE_THROUGH_SPAN, 0, task.text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            vh.textView.setText(task.text);
        }
        vh.textView.setTag(task.getId());
        vh.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openClickHandle(TaskEntity.load(TaskEntity.class, (Long) view.getTag()));
            }
        });

        // delete btn
        if (vh.deleteBtn.getTag() != task.getId()) {
            vh.deleteBtn.setTag(task.getId());
            vh.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteClickHandle(TaskEntity.load(TaskEntity.class, (Long) view.getTag()));
                }
            });
        }


        return convertView;
    }

    public void openClickHandle(TaskEntity task) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra("task_id", task.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void checkboxClickHandle(View view) {
        TaskEntity task = TaskEntity.load(TaskEntity.class, (Long) view.getTag());
        CheckBox checkBox = (CheckBox) view;
        RelativeLayout parentView = (RelativeLayout) view.getParent();

        TextView textView = (TextView) parentView.findViewById(R.id.text);
        String text = textView.getText().toString();

        if (checkBox.isChecked()) {
            textView.setText(text, TextView.BufferType.SPANNABLE);
            Spannable spannable = (Spannable) textView.getText();
            spannable.setSpan(STRIKE_THROUGH_SPAN, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            textView.setText(text);
        }

        task.isDone = checkBox.isChecked();
        task.save();
    }

    public void deleteClickHandle(TaskEntity task) {
        task.delete();
        this.notifyDataSetChanged();
    }

    private static class ViewHolder {
        RelativeLayout layout;
        CheckBox checkBox;
        TextView textView;
        ImageButton deleteBtn;

        public ViewHolder(View convertView) {
            layout = (RelativeLayout) convertView.findViewById(R.id.relativeLayout);
            checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            textView = (TextView) convertView.findViewById(R.id.text);
            deleteBtn = (ImageButton) convertView.findViewById(R.id.deleteButton);
        }
    }
}
