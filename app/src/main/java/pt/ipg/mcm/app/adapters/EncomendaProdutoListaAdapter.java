package pt.ipg.mcm.app.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import pt.ipg.mcm.app.R;
import pt.ipg.mcm.app.bd.EncomendaProduto;

import java.util.List;

import static java.lang.String.format;
import static pt.ipg.mcm.app.util.Formatter.centsToEuros;

public class EncomendaProdutoListaAdapter extends BaseAdapter {

    private List<EncomendaProduto> data;
    private Context context;

    public EncomendaProdutoListaAdapter(Context context, List<EncomendaProduto> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public EncomendaProduto getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int pos, View view, ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.swipe_item, parent, false);
            view.setBackgroundColor(context.getResources().getColor(R.color.abc_background_cache_hint_selector_material_dark));
        }

        TextView textNome = (TextView) view.findViewById(R.id.peiVlNome);
        TextView qtdEPreco = (TextView) view.findViewById(R.id.peiVlQtdePreco);

        EncomendaProduto item = this.getItem(pos);

        String strQtdEPreco = "%d x %s = %s";
        final String strPrecoUnitario = centsToEuros(item.getPrecoUnitario());
        final String strTotal = centsToEuros(item.getPrecoUnitario() * item.getQuantidade().longValue());
        strQtdEPreco = format(strQtdEPreco, item.getQuantidade(), strPrecoUnitario, strTotal);

        byte[] foto = item.getProduto().getFoto();
        if (foto != null) {
            final Resources resources = view.getContext().getResources();
            final Bitmap bitmap = BitmapFactory.decodeByteArray(foto, 0, foto.length);
            BitmapDrawable bmp = new BitmapDrawable(resources, bitmap);
            ImageView image = (ImageView) view.findViewById(R.id.peiImgFoto);
            image.setImageDrawable(bmp);
        }

        textNome.setText(item.getProduto().getNome());
        qtdEPreco.setText(strQtdEPreco);
        return view;
    }


}
