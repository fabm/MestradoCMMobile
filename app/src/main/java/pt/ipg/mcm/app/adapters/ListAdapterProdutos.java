package pt.ipg.mcm.app.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import pt.ipg.mcm.app.R;
import pt.ipg.mcm.app.bd.DaoMaster;
import pt.ipg.mcm.app.bd.DaoSession;
import pt.ipg.mcm.app.bd.Produto;
import pt.ipg.mcm.app.instances.App;
import pt.ipg.mcm.calls.client.model.localidades.LocalidadeRest;

import java.util.List;

public class ListAdapterProdutos extends BaseAdapter {

  private final Context context;
  protected List<Produto> data;

  public ListAdapterProdutos(Context context) {
    SQLiteDatabase db = App.get().getOpenHelper(context).getReadableDatabase();
    DaoSession session = new DaoMaster(db).newSession();
    data = session.getProdutoDao().loadAll();
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
      view = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
    }
    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
    TextView text2 = (TextView) view.findViewById(android.R.id.text2);

    Produto item = (Produto) getItem(pos);


    text2.setText(item.getNome());
    text1.setText(item.getPrecoActual().toString());

    return view;
  }

  public Produto getProduto(int position){
    return data.get(position);
  }

}