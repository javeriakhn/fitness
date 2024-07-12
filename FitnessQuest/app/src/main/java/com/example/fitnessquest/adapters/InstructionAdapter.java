package com.example.fitnessquest.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.fitnessquest.R;

import java.util.List;

public class InstructionAdapter extends ArrayAdapter<String> {

    private List<String> instructions;
    private LayoutInflater inflater;

    public InstructionAdapter(Context context, List<String> instructions) {
        super(context, 0, instructions);
        this.instructions = instructions;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.instruction_list_layout, parent, false);
        }

        String instruction = instructions.get(position);
        TextView textViewInstruction = convertView.findViewById(R.id.tv_instruction);
        textViewInstruction.setText(instruction);

        return convertView;
    }
}
