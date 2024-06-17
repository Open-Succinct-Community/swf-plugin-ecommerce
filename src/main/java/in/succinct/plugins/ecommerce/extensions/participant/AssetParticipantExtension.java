package in.succinct.plugins.ecommerce.extensions.participant;

import com.venky.swf.db.model.User;
import com.venky.swf.db.model.reflection.ModelReflector;
import com.venky.swf.plugins.collab.extensions.participation.CompanySpecificParticipantExtension;
import com.venky.swf.pm.DataSecurityFilter;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import in.succinct.plugins.ecommerce.db.model.assets.Asset;
import in.succinct.plugins.ecommerce.db.model.catalog.Item;
import in.succinct.plugins.ecommerce.db.model.catalog.UnitOfMeasure;

import java.util.List;

public class AssetParticipantExtension extends CompanySpecificParticipantExtension<Asset> {
	static  {
		registerExtension(new AssetParticipantExtension());
	}
	@Override
	public List<Long> getAllowedFieldValues(User user, Asset partiallyFilledModel, String fieldName) {
		return super.getAllowedFieldValues(user,partiallyFilledModel,fieldName);
	}

}
