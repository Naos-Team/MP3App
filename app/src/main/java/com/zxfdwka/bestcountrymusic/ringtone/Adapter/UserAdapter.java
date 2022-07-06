package com.zxfdwka.bestcountrymusic.ringtone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.zxfdwka.bestcountrymusic.R;
import com.zxfdwka.bestcountrymusic.ringtone.DBHelper.DBHelper;
import com.zxfdwka.bestcountrymusic.ringtone.Listener.ClickListenerRecorder;
import com.zxfdwka.bestcountrymusic.ringtone.Method.Methods;
import com.zxfdwka.bestcountrymusic.ringtone.item.ListltemUser;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter {
    private DBHelper dbHelper;
    private ArrayList<ListltemUser> arrayList;
    private ArrayList<ListltemUser> filteredArrayList;
    private NameFilter filter;
    private Context context;
    private Methods methods;
    private ClickListenerRecorder recyclerClickListener;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private int columnWidth = 0;

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title;

        private MyViewHolder(View view) {
            super(view);
            tv_title = itemView.findViewById(R.id.tv_name);
        }
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        private static ProgressBar progressBar;

        private ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }

    public UserAdapter(Context context, ArrayList<ListltemUser> arrayList, ClickListenerRecorder recyclerClickListener) {
        this.arrayList = arrayList;
        this.context = context;
        this.filteredArrayList = arrayList;

        this.recyclerClickListener = recyclerClickListener;
        dbHelper = new DBHelper(context);
        methods = new Methods(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_ringtone, parent, false);
            return new MyViewHolder(itemView);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_progressbar_ringtone, parent, false);
            return new ProgressViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {
            final ListltemUser song = arrayList.get(position);

            ((MyViewHolder) holder).tv_title.setText(song.getName());

        } else {
            if (getItemCount() == 1) {
                ProgressViewHolder.progressBar.setVisibility(View.GONE);
            }
        }
    }

    public String format(Number number) {
        char[] arrc = new char[]{' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long l = number.longValue();
        double d = l;
        int n = (int)Math.floor((double)Math.log10((double)d));
        int n2 = n / 3;
        if (n >= 3 && n2 < arrc.length) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(new DecimalFormat("#0.0").format(d / Math.pow((double)10.0, (double)(n2 * 3))));
            stringBuilder.append(arrc[n2]);
            return stringBuilder.toString();
        }
        return new DecimalFormat("#,##0").format(l);
    }

    @Override
    public int getItemCount() {
        return arrayList.size() + 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private int getPosition(String id) {
        int count=0;
        for(int i=0;i<filteredArrayList.size();i++) {
            if(id.equals(filteredArrayList.get(i).getUid())) {
                count = i;
                break;
            }
        }
        return count;
    }

    public Filter getFilter() {
        if (filter == null) {
            filter = new NameFilter();
        }
        return filter;
    }

    private class NameFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint.toString().length() > 0) {
                ArrayList<ListltemUser> filteredItems = new ArrayList<>();

                for (int i = 0, l = filteredArrayList.size(); i < l; i++) {
                    String nameList = filteredArrayList.get(i).getName();
                    if (nameList.toLowerCase().contains(constraint))
                        filteredItems.add(filteredArrayList.get(i));
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    result.values = filteredArrayList;
                    result.count = filteredArrayList.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrayList = (ArrayList<ListltemUser>) results.values;
            notifyDataSetChanged();
        }
    }


    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    public boolean isHeader(int position) {
        return position == arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? VIEW_PROG : VIEW_ITEM;
    }

}