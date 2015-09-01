package pt.ipg.mcm.app.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import pt.ipg.mcm.app.bd.DaoMaster;
import pt.ipg.mcm.app.bd.DaoSession;
import pt.ipg.mcm.app.bd.EncomendaProduto;
import pt.ipg.mcm.app.bd.EncomendaProdutoDao;
import pt.ipg.mcm.app.bd.Produto;
import pt.ipg.mcm.app.instances.App;

import java.util.List;

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
    for(EncomendaProduto encomendaProduto:data){
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
      view = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
    }
    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
    TextView text2 = (TextView) view.findViewById(android.R.id.text2);

    EncomendaProduto item = (EncomendaProduto) getItem(pos);

    text2.setText(item.getQuantidade().toString());
    text1.setText(item.getProduto().getNome());

    return view;
  }

}