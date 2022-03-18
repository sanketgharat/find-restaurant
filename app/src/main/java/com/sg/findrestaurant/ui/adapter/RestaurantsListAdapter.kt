package com.sg.findrestaurant.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sg.findrestaurant.R
import com.sg.findrestaurant.data.model.Businesses
import com.sg.findrestaurant.databinding.LayoutLoadingItemBinding
import com.sg.findrestaurant.databinding.RestaurantRecyclerItemLayoutBinding

class RestaurantsListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TAG = "RestaurantsListAdapter"
        private const val ITEM = 0
        private const val LOADING = 1
    }

    private var list: MutableList<Businesses> = mutableListOf()

    private var isLoadingAdded = false

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<Businesses>) {
        Log.d(TAG, "setList: ")
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val viewHolder: RecyclerView.ViewHolder

        when (viewType) {
            ITEM -> {
                Log.d(TAG, "onCreateViewHolder: ITEM")
                val binding =
                    RestaurantRecyclerItemLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )

                viewHolder = MyViewHolder(binding)
            }
            else -> {
                Log.d(TAG, "onCreateViewHolder: Loader")
                val binding = LayoutLoadingItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                viewHolder = MyViewHolderLoading(binding)
            }
        }

        return viewHolder
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (getItemViewType(position) == ITEM) {
            Log.d(TAG, "onBindViewHolder: ITEM")

            val item = list[position]

            with(holder as MyViewHolder) {
                binding.textRestaurantName.text = item.name

                Glide.with(binding.imageRestaurantIcon.context).load(item.imageUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(binding.imageRestaurantIcon)

                binding.textRating.text = item.rating.toString()
                val context = binding.textRestaurantDistanceAddressLine.context
                val distance = item.distance.toInt()
                var address : String? = ""
                if(item.location != null) {
                    address = item.location!!.address1
                    if (address == null || address.isEmpty()) {
                        address = item.location!!.city
                    }
                }
                binding.textRestaurantDistanceAddressLine.text =
                    context.getString(R.string.distance_address_line, distance, address)
                binding.textRestaurantStatus.text =
                    if (item.isClosed) context.getString(R.string.restaurant_status_closed) else context.getString(
                        R.string.restaurant_status_open
                    )

            }
        }else{
            Log.d(TAG, "onBindViewHolder: LOADER")
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(val binding: RestaurantRecyclerItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class MyViewHolderLoading(val binding: LayoutLoadingItemBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun getItemViewType(position: Int): Int {
        return if (position == list.size - 1 && isLoadingAdded) LOADING else ITEM
    }

    fun add(item: Businesses) {
        Log.d(TAG, "add")
        list.add(item)
        notifyItemInserted(list.size - 1)
    }

    fun addAll(list: List<Businesses>) {
        Log.d(TAG, "addAll")
        /*for (item in list) {
            add(item)
        }*/
        val oldSize = this.list.size
        val newToAdd = list.size
        this.list.addAll(list)
        notifyItemRangeInserted(oldSize, newToAdd)
    }

    fun remove(item: Businesses?) {
        Log.d(TAG, "remove")
        if (item == null) {
            return
        }
        val position: Int = list.indexOf(item)
        if (position > -1) {
            list.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        Log.d(TAG, "clear")
        isLoadingAdded = false
        while (itemCount > 0) {
            remove(getItem(0))
        }
    }

    fun isEmpty(): Boolean {
        Log.d(TAG, "isEmpty")
        return itemCount == 0
    }

    fun addLoadingFooter() {
        Log.d(TAG, "addLoadingFooter")
        isLoadingAdded = true
        add(Businesses())
    }

    fun removeLoadingFooter() {
        Log.d(TAG, "removeLoadingFooter")
        isLoadingAdded = false
        val position: Int = list.size - 1
        val item: Businesses? = getItem(position)
        if (item != null) {
            list.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun getItem(position: Int): Businesses? {
        return if (position == -1) {
            null
        } else list[position]
    }


}