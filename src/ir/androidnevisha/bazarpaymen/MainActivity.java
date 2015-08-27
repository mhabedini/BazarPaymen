package ir.androidnevisha.bazarpaymen;

import ir.androidnevisha.bazarpaymen.util.IabHelper;
import ir.androidnevisha.bazarpaymen.util.IabResult;
import ir.androidnevisha.bazarpaymen.util.Inventory;
import ir.androidnevisha.bazarpaymen.util.Purchase;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.content.Intent;
import android.os.Bundle;



@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity implements OnClickListener{

	Button payBtn;
	
	// Debug tag, for logging
	static final String TAG = "bazar Payment in billing app";

	// SKUs for our products: the premium upgrade (non-consumable)
	static final String SKU_PREMIUM = "bazarpayment";

	// Does the user have the premium upgrade?
	boolean mIsPremium = false;

	// (arbitrary) request code for the purchase flow
	static final int RC_REQUEST = 1234;

	// The helper object
	IabHelper mHelper;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		payBtn = (Button) findViewById(R.id.pay_btn);
		payBtn.setOnClickListener(this);
		
		
		String base64EncodedPublicKey = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwC64KQCGPhcMdjUvGsOgVVWRMaMEfDnTvfLUgNaWtLHeu51aClNwU/57NVCFBvmv2SjED0yjcEK9vcu/NBNOxbB4Bv4mC7qALWr2LcV4USUzsNtypNVeyDLf9s4CkgrbHBoVVfMQS4vnKMXtRlx/ODiG9ADIOsKGG/4U6zeqRl7OGyOPf3ny3Ssb27becA04ggElqON3dgd3xlatP6vcsx3d6WgPu+XYAepGUlDp60CAwEAAQ==";
		// You can find it in your Bazaar console, in the Dealers section. 
		// It is recommended to add more security than just pasting it in your source code;
		mHelper = new IabHelper(this, base64EncodedPublicKey);

		Log.i(TAG, "Starting setup.");
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
		    public void onIabSetupFinished(IabResult result) {
		        Log.i(TAG, "Setup finished.");

		        if (!result.isSuccess()) {
		            // Oh noes, there was a problem.
		            Log.i(TAG, "Problem setting up In-app Billing: " + result);
		        }
		        // Hooray, IAB is fully set up!
		        mHelper.queryInventoryAsync(mGotInventoryListener);
		    }
		});
		
		
	}
	
	
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
	    public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
	        Log.d(TAG, "Query inventory finished.");
	        if (result.isFailure()) {
	            Log.i(TAG, "Failed to query inventory: " + result);
	            return;
	        }
	        else {
	            Log.i(TAG, "Query inventory was successful.");
	            // does the user have the premium upgrade?
	            mIsPremium = inventory.hasPurchase(SKU_PREMIUM);

	            // update UI accordingly

	            Log.i(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
	        }

	        Log.i(TAG, "Initial inventory query finished; enabling main UI.");
	    }

	};

	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
	    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
	        if (result.isFailure()) {
	            Log.i(TAG, "Error purchasing: " + result);
	            return;
	        }
	        else if (purchase.getSku().equals(SKU_PREMIUM)) {
	            // give user access to premium content and update the UI
	        }
	    }


	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

	    Log.i(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

	    // Pass on the activity result to the helper for handling
	    if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
	        super.onActivityResult(requestCode, resultCode, data);
	    } else {
	        Log.i(TAG, "onActivityResult handled by IABUtil.");
	    }
	}
	
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch (v.getId()) {
		case R.id.pay_btn:
			
			mHelper.launchPurchaseFlow(this, SKU_PREMIUM, RC_REQUEST, mPurchaseFinishedListener, "payload-string");
			
			break;

		default:
			break;
		}
		
		
	}

	
	
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    if (mHelper != null) mHelper.dispose();
	    mHelper = null;
	}
	
	
	
}
