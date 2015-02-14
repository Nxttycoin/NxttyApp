package com.nxtapp.fragment;

import com.nxtty.main.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class AddTopicActivity extends Activity
{

    ImageView ivback;
    EditText editName, editDescription;
    Spinner spinCat;
    LinearLayout lnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.topic_add);
	LoadUI();
    }

    private void LoadUI()
    {

	ivback = (ImageView) findViewById(R.id.iv_Back_topic);
	editName = (EditText) findViewById(R.id.edit_topic_name);
	editDescription = (EditText) findViewById(R.id.edit_topic_description);

	spinCat = (Spinner) findViewById(R.id.spin_topic_cat);
	
//	ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, R.string.topic_categories_array);
//	dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//	spinCat.setAdapter(dataAdapter);
//	spinCat.setOnItemSelectedListener(new OnItemSelectedListener()
//	{
//	    @Override
//	    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
//	    {
//	    }
//
//	    @Override
//	    public void onNothingSelected(AdapterView<?> parent)
//	    {
//
//	    }
//	});

    }
}
