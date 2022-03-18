package com.sg.findrestaurant.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sg.findrestaurant.R
import com.sg.findrestaurant.data.model.Businesses
import com.sg.findrestaurant.databinding.RestaurantRecyclerItemLayoutBinding

class RestaurantsListAdapter : RecyclerView.Adapter<RestaurantsListAdapter.MyViewHolder>() {

    companion object {
        private const val TAG = "RestaurantsListAdapter"
    }

    private var list: List<Businesses> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<Businesses>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val binding =
            RestaurantRecyclerItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return MyViewHolder(binding)
    }


    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int,
        payloads: List<Any?>
    ) {

        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            updateView(holder, position, payloads[0])
        }
    }

    private fun updateView(holder: MyViewHolder, position: Int, any: Any?) {
        //for single item ui update with data
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]

        with(holder) {
            binding.textRestaurantName.text = item.name

            Glide.with(binding.imageRestaurantIcon.context).load(item.imageUrl)
                .into(binding.imageRestaurantIcon)

            binding.textRating.text = item.rating.toString()
            val context = binding.textRestaurantDistanceAddressLine.context
            val distance = item.distance.toInt()
            val address = if (item.location != null) item.location!!.address1 else ""
            binding.textRestaurantDistanceAddressLine.text =
                context.getString(R.string.distance_address_line, distance, address)
            binding.textRestaurantStatus.text =
                if (item.isClosed) context.getString(R.string.restaurant_status_closed) else context.getString(
                    R.string.restaurant_status_open
                )

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(var binding: RestaurantRecyclerItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)


}