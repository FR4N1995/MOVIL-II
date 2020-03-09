package com.osvaldovillalobosperez.misclientesimageneshttpp87b;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

public class RecyclerViewAdaptador extends RecyclerView.Adapter<RecyclerViewAdaptador.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private NetworkImageView networkImageView;
        private TextView textView;
        private ImageLoader imageLoader;
        //private Context context;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            networkImageView = itemView.findViewById(R.id.imgCargarImagen);
            textView = itemView.findViewById(R.id.txtCargarTexto);
            imageLoader = MySingleton.getInstance(itemView.getContext()).getImageLoader();
        }
    }

    public List<Card> cardList;

    public RecyclerViewAdaptador(List<Card> cardList) {
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.card,
                parent,
                false
        );
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.textView.setText(cardList.get(position).getName());
        holder.imageLoader.get(
                cardList.get(position).getImgUrl(),
                ImageLoader.getImageListener(
                        holder.networkImageView,
                        R.mipmap.ic_launcher,
                        android.R.drawable.alert_dark_frame
                )
        );
        holder.networkImageView.setImageUrl(cardList.get(position).getImgUrl(), holder.imageLoader);
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }
}
