    package com.vishal.mysocialapp;

    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.TextView;
    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;
    import com.bumptech.glide.Glide;
    import java.util.List;
    import de.hdodenhof.circleimageview.CircleImageView;

    public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
        private List<Comment> commentList;

        public CommentAdapter(List<Comment> commentList) {
            this.commentList = commentList;
        }

        @NonNull
        @Override
        public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
            Comment comment = commentList.get(position);
            holder.usernameTextView.setText(comment.getUsername());
            holder.commentTextView.setText(comment.getCommentText());
            Glide.with(holder.avatarImageView.getContext()).load(comment.getAvatarUrl()).into(holder.avatarImageView);
        }

        @Override
        public int getItemCount() {
            return commentList.size();
        }

        public static class CommentViewHolder extends RecyclerView.ViewHolder {
            CircleImageView avatarImageView;
            TextView usernameTextView, commentTextView;

            public CommentViewHolder(@NonNull View itemView) {
                super(itemView);
                avatarImageView = itemView.findViewById(R.id.avatarImageView);
                usernameTextView = itemView.findViewById(R.id.usernameTextView);
                commentTextView = itemView.findViewById(R.id.commentTextView);
            }
        }
    }
