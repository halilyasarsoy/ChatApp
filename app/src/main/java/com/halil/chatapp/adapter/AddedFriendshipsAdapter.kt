//package com.halil.chatapp.adapter
//
//import androidx.recyclerview.widget.RecyclerView
//import com.halil.chatapp.data.Users
//
//class AddedFriendshipsAdapter (private var approvedFriends: ArrayList<Users>) :
//    RecyclerView.Adapter<MutualFriendsAdapter.ViewHolder>() {
//
//    class ViewHolder(private val binding: ListItemBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        fun bind(user: User) {
//            binding.tvFirstName.text = user.name
//            binding.tvLastName.text = user.lastname
//            binding.imgUser.load(user.imgUrl)
//            binding.tvLatestMessage.text = user.profession
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bind(approvedFriends[position])
//    }
//
//    override fun getItemCount(): Int = approvedFriends.size
//
//    fun updateApprovedFriends(newList: List<User>) {
//        approvedFriends.clear()
//        approvedFriends.addAll(newList)
//        notifyDataSetChanged()
//    }
//}