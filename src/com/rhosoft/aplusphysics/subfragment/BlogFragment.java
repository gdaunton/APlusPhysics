package com.rhosoft.aplusphysics.subfragment;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import nl.matshofman.saxrssreader.RssItem;
import nl.matshofman.saxrssreader.RssReader;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.rhosoft.aplusphysics.FeedDetail;
import com.rhosoft.aplusphysics.R;
import com.rhosoft.aplusphysics.items.FeedListAdaptor;
import com.rhosoft.aplusphysics.items.FeedObject;

public class BlogFragment extends SherlockFragment{

	public static BlogFragment newInstance() {
		return new BlogFragment();
	}

	public ArrayList<FeedObject> blog_objects;
	private ListView lv;
	private View bar;

	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.blogs, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle saved){
		super.onViewCreated(view, saved);
		this.blog_objects = new ArrayList<FeedObject>();
		this.lv = ((ListView) getView().findViewById(R.id.listView));
		this.bar = getView().findViewById(R.id.progress);
		this.bar.setVisibility(4);
		new LoadRss().execute();
		this.lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> paramAnonymousAdapterView,View paramAnonymousView, int paramAnonymousInt,long paramAnonymousLong) {
				FeedObject localFeedObject = (FeedObject) BlogFragment.this.blog_objects.get(paramAnonymousInt);
				Intent localIntent = new Intent();
				localIntent.setClass(BlogFragment.this.getActivity(), FeedDetail.class);
				int breakValue = 0;
				for(int i =0; i < localFeedObject.Title.length(); i++){
					if(localFeedObject.Title.charAt(i) == '-'){
						breakValue = i+1;
						break;
					}
				}
				localIntent.putExtra("Title", localFeedObject.Title.substring(0, breakValue-1));
				localIntent.putExtra("SubTitle", localFeedObject.Title.substring(breakValue));
				localIntent.putExtra("URL", localFeedObject.link);
				BlogFragment.this.startActivity(localIntent);
			}
		});
		this.lv.setAdapter(new FeedListAdaptor(getActivity().getLayoutInflater(), this.blog_objects));
		
	}
	
	private class LoadRss extends AsyncTask<Void, Void, Void> {
		private ArrayList<RssItem> blogRssItems;
		private boolean isError = false;

		private LoadRss() {
		}

		protected Void doInBackground(Void...voids) {
			try {
				URL localURL = new URL(
						"http://aplusphysics.com/community/index.php/rss/blog/");
				new RssReader();
				this.blogRssItems = RssReader.read(localURL).getRssItems();
				return null;
			} catch (Exception localException) {
				while (true)
					this.isError = true;
			}
		}

		protected void onPostExecute(Void paramVoid) {
			try{
				((ProgressBar)getView().findViewById(R.id.progress)).setVisibility(4);
			}catch(Exception e){}
			if (this.isError) {
				TextView localTextView = new TextView(BlogFragment.this.getActivity());
				localTextView.setText("Rss feed unavailable");
				((FrameLayout) BlogFragment.this.getView().findViewById(R.id.layout)).addView(localTextView);
				return;
			}
			Iterator<RssItem> localIterator = this.blogRssItems.iterator();
			while (true) {
				if (!localIterator.hasNext()) {
					((FeedListAdaptor) BlogFragment.this.lv.getAdapter()).notifyDataSetChanged();
					return;
				}
				RssItem localRssItem = (RssItem) localIterator.next();
				String str = new SimpleDateFormat("MM/dd/yyyy hh:mm aa").format(localRssItem.getPubDate());
				BlogFragment.this.blog_objects.add(new FeedObject(localRssItem.getTitle(), str, localRssItem.getLink()));
			}
		}

		protected void onPreExecute() {
			BlogFragment.this.bar.setVisibility(0);
		}
	}
	
}
