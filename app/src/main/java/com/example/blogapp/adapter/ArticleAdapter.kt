import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blogapp.databinding.ArticleItemBinding
import com.example.blogapp.model.BlogItemModel

class ArticleAdapter(
    private val context: Context,
    private var blogList: List<BlogItemModel>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<ArticleAdapter.BlogViewHolder>() {

    interface OnItemClickListener {
        fun onEditClick(blogItem: BlogItemModel)
        fun onReadMoreClick(blogItem: BlogItemModel)
        fun onDeleteClick(blogItem: BlogItemModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ArticleItemBinding.inflate(inflater, parent, false)
        return BlogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blogItem = blogList[position]
        holder.bind(blogItem)
    }

    override fun getItemCount(): Int {
        return blogList.size
    }

    fun setData(blogSaveList: List<BlogItemModel>) {
        this.blogList = blogSaveList
        notifyDataSetChanged()
    }

    inner class BlogViewHolder(private val binding: ArticleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(blogItem: BlogItemModel) {
            binding.apply {
                heading.text = blogItem.heading
                Glide.with(binding.profile.context)
                    .load(blogItem.profileImage)
                    .into(profile)
                userName.text = blogItem.userName
                date.text = blogItem.date
                post.text = blogItem.post

                // Set click listeners if needed


                binding.readMoreButton.setOnClickListener {
                    itemClickListener.onReadMoreClick(blogItem)
                }

                binding.editButton.setOnClickListener {
                    itemClickListener.onEditClick(blogItem)
                }
                binding.deleteButton.setOnClickListener {
                    itemClickListener.onDeleteClick(blogItem)
                }
            }
        }
    }
}
