package com.cryptobuddy.ryanbridges.cryptobuddy.news;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.cryptobuddy.ryanbridges.cryptobuddy.views.NewsView;

import java.util.List;


/**
 * Created by Ryan on 8/13/2017.
 */

/*TODO: I just blasted away the classic viewHolder implementation. The problem you were
 having was caused by the viewholder itself, since it forced you to keep references to
 the items for the listener. This IS an OPINION, a well thought one, but an opinion nonetheless.
 Let's go

 ViewHolder is an antipattern. Like I explained to you before, the UI kit of android is identical to
 java swift. That means, every widget is an object, and in java, every object has a class, usually
 an open class. In swift, every window was a subclass oj JFrame, which contained inside references to
 every widget added via the Drag and Drop tool, and therefore allowed to deal with the widgets
 through this references. When you inflate an android XML, you are summoning a generic factory which
 looks into all the android.view.View subclasses, instantiates every matching class from the XML file,
 and adds them to the first tag class instance (and/or the parent), and then returns the result as a
 View instance... identical to a JFrame, but with every children anonymized. Therefore, instead of
 dealing with anonymous View instances by inflation and externalized references using the ID, you can
 subclass any layout (Layout->Viewgroup->View), put the references there, inflate the xml file
 inside the layout subclass, and add a sort of model class to provide the values to set into your
 widgets and keep al the view-related code inside the view itself.
  The ViewHolder pattern came to be due to the vast number of android programmers lacking java/POO
  knowledge. The old List widget, ListView, created/recycled the cells using one method:

  public View getView(int position, View convertView, ViewGroup parent)

  This method was where the view inflation happened. The convertView object was a previous cell,
  now unused. These programmers never realized the relationship between XML files and view classes,
  and never though about resetting the widgets contained within convertView, they just kept inflating
  XML over and over, killing performance in the process. Therefore, ViewHolder was created and
  enforced. References to the views were put inside holders, and convertView was hidden
  (viewHolder in fact kepts a reference to the anonymous viewgroup, itemView). That brought the kind
  of problem you had, ¿How to fire events using the data rendered in the widgets, if holders are
  shared between all the data instances and can change at any minute?
   The answer is, avoid the holder. View events belong to the Views. it's simpler, faster (you avoid
   creating a useless hollow class instance, you can use tricks like flattening (the merge tag,
   which essentially tells the inflater "please ignore me, I'm just a placeholder. After creating
   all my children instances, pass them to the parent"), and asynchronous inflation/instantiation,
   create varations of your XML files using the same view code, and allow you to reuse view code way
   more easily.
 */
public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.holder> {

    private List<NewsItem> newsList;

    public NewsListAdapter(List<NewsItem> newsList) {
        this.newsList = newsList;
    }

    public NewsListAdapter setData(List<NewsItem> newsList){
        this.newsList = newsList;
        this.notifyDataSetChanged();
        return this;
    }

    @Override
    public holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new holder(new NewsView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(holder holder, int position) {
        holder.getView().setData(newsList.get(position));
    }

    public static class holder extends RecyclerView.ViewHolder {

        private holder(NewsView itemLayoutView) {
            super(itemLayoutView);
        }

        public NewsView getView(){
            return (NewsView) itemView;
        }

    }

    public int getItemCount() {
        return newsList.size();
    }
}
