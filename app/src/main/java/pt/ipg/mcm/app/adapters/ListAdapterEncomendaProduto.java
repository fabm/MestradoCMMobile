package pt.ipg.mcm.app.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
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
import pt.ipg.mcm.app.bd.DaoMaster;
import pt.ipg.mcm.app.bd.DaoSession;
import pt.ipg.mcm.app.bd.EncomendaProduto;
import pt.ipg.mcm.app.bd.EncomendaProdutoDao;
import pt.ipg.mcm.app.instances.App;
import pt.ipg.mcm.app.util.Formatter;

import java.util.List;

import static java.lang.String.format;
import static pt.ipg.mcm.app.util.Formatter.centsToEuros;

public class ListAdapterEncomendaProduto extends BaseAdapter {

    private final Context context;
    protected List<EncomendaProduto> data;

    public ListAdapterEncomendaProduto(Context context, long idEncomenda) {
        SQLiteDatabase db = App.get().getOpenHelper(context).getReadableDatabase();
        DaoSession session = new DaoMaster(db).newSession();
        data = session.getEncomendaProdutoDao()
                .queryBuilder()
                .where(EncomendaProdutoDao.Properties.IdEncomenda.eq(idEncomenda))
                .build()
                .list();
        for (EncomendaProduto encomendaProduto : data) {
            encomendaProduto.getProduto();
        }
        db.close();
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.produto_encomendado_item, parent, false);
        }
        TextView textNome = (TextView) view.findViewById(R.id.peiVlNome);
        TextView qtdEPreco = (TextView) view.findViewById(R.id.peiVlQtdePreco);

        EncomendaProduto item = (EncomendaProduto) getItem(pos);

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