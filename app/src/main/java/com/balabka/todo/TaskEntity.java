package com.balabka.todo;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "task")
public class TaskEntity extends Model {

    @Column(name = "is_done")
    public boolean isDone;

    @Column(name = "text")
    public String text;

    public String toString() {
        return "Checked: " + isDone +
                ", Text: " + text;
    }

    public boolean hasText() {
        return (text != null && !text.equals(""));
    }
}
