package com.timelesssoftware.googleplayrevyclerview;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.timelesssoftware.gplayrecyclerview.GPlayRecyclerView;

public class MainActivity extends AppCompatActivity {

    GPlayRecyclerView gPlayRecyclerView_large;
    private GPlayRecyclerView gPlayRecyclerView_small;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        gPlayRecyclerView_small = findViewById(R.id.google_play_rv);
        gPlayRecyclerView_small.setAdatper(new GAdapter(R.layout._g_view_holder));
        gPlayRecyclerView_small.getAdapter().notifyDataSetChanged();
        gPlayRecyclerView_small.getState();
        gPlayRecyclerView_small.enableDefaultSnapHelper();

        gPlayRecyclerView_large = findViewById(R.id.google_play_rv_2);
        gPlayRecyclerView_large.setAdatper(new GAdapter(R.layout._g_view_holder_2));
        gPlayRecyclerView_large.getAdapter().notifyDataSetChanged();
        gPlayRecyclerView_large.getState();
        gPlayRecyclerView_large.setEnableBacgroundAlpha(false);
        gPlayRecyclerView_large.setEnableBackgroundMove(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class GAdapter extends RecyclerView.Adapter<GAdapter.GVHolder> {

        private final int viewId;

        public GAdapter(int view) {
            this.viewId = view;
        }

        @Override
        public GAdapter.GVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(this.viewId, parent, false);
            return new GVHolder(view);
        }

        @Override
        public void onBindViewHolder(GAdapter.GVHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 10;
        }

        public class GVHolder extends RecyclerView.ViewHolder {
            private final TextView textView;

            public GVHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.text);
                textView.setText("this is text " + getAdapterPosition());
            }
        }
    }
}
