package com.example.rent_it.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rent_it.CommentsActivity;
import com.example.rent_it.DetailsActivity;
import com.example.rent_it.Model.Post;
import com.example.rent_it.Model.User;
import com.example.rent_it.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public Context mContext;
    public List<Post> mPost;
    private FirebaseUser firebaseUser;

    public PostAdapter(Context context, List<Post> postLists) {
        this.mContext = context;
        this.mPost = postLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Post post = mPost.get(position);

        try {
            Glide.with(mContext).load(post.getImage()).into(holder.post_image);

            if (post.getDescription() != null && !post.getDescription().isEmpty()) {
                holder.description.setVisibility(View.VISIBLE);
                holder.description.setText(post.getDescription());
            } else {
                holder.description.setVisibility(View.GONE);
            }

            if (post.getTitle() != null && !post.getTitle().isEmpty()) {
                holder.title.setVisibility(View.VISIBLE);
                holder.title.setText(post.getTitle());
            } else {
                holder.title.setVisibility(View.GONE);
            }

            if (post.getEmail() != null && !post.getEmail().isEmpty()) {
                holder.email.setVisibility(View.VISIBLE);
                holder.email.setText(post.getEmail());
            } else {
                holder.email.setVisibility(View.GONE);
            }

            publisherInfo(holder.image_profile, holder.username, holder.email, holder.publisher, post.getPublisher());
            isLiked(post.getPostId(), holder.like);
            nrLikes(holder.likes, post.getPostId());
            getComments(post.getPostId(), holder.comments);

            holder.like.setOnClickListener(v -> {
                if (holder.like.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(post.getPostId()).child(firebaseUser.getUid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(post.getPostId()).child(firebaseUser.getUid()).removeValue();
                }
            });

            View.OnClickListener commentsClickListener = v -> {
                Intent intent = new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postid", post.getPostId());
                intent.putExtra("publisherid", post.getPublisher());
                mContext.startActivity(intent);
            };

            holder.comments.setOnClickListener(commentsClickListener);
            holder.comment.setOnClickListener(commentsClickListener);

            holder.itemView.setOnClickListener(v -> {
                Log.d("POST_CLICK", "Нажатие на пост: " + post.getTitle());
                Intent intent = new Intent(mContext, DetailsActivity.class);
                intent.putExtra("postId", post.getPostId());
                intent.putExtra("title", post.getTitle());
                intent.putExtra("description", post.getDescription());
                intent.putExtra("price", post.getPrice());
                intent.putExtra("location", post.getLocation());
                intent.putExtra("author", post.getEmail());
                intent.putExtra("image", post.getImage());
                intent.putExtra("bedrooms", post.getBedrooms());
                intent.putExtra("bathrooms", post.getBathrooms());
                intent.putExtra("area", post.getArea());
                intent.putExtra("type", post.getType());
                intent.putExtra("amenities", post.getAmenities());
                intent.putExtra("rating", post.getRating());
                mContext.startActivity(intent);
            });

        } catch (Exception e) {
            Log.d("PostError", "PostError: " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image_profile, post_image, like, comment, save;
        public TextView username, likes, publisher, description, comments, title, email, location;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);

            username = itemView.findViewById(R.id.username);
            likes = itemView.findViewById(R.id.likes);
            publisher = itemView.findViewById(R.id.publisher);
            email = itemView.findViewById(R.id.email);

            description = itemView.findViewById(R.id.description);
            title = itemView.findViewById(R.id.title);
            location = itemView.findViewById(R.id.location);
            comments = itemView.findViewById(R.id.comments);
        }
    }

    private void getComments(String postid, TextView comments) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.setText("View All " + snapshot.getChildrenCount() + " Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void isLiked(String postId, ImageView imageView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void nrLikes(TextView likes, String postId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount() + " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void publisherInfo(ImageView image_profile, TextView userName, TextView email, TextView publisher, String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    try {
                        Glide.with(mContext).load(user.getImageUrl()).into(image_profile);
                        userName.setText(user.getUserName());
                        publisher.setText(user.getUserName());
                        email.setText(user.getEmail());
                    } catch (Exception e) {
                        Log.d("PostError", "Error loading user data: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
