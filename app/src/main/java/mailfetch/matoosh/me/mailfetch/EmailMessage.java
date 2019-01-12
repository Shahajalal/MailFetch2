package mailfetch.matoosh.me.mailfetch;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EmailMessage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EmailMessage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmailMessage extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "message";
    private static final String ARG_PARAM2 = "recipient";
    private static final String ARG_PARAM3 = "topic";
    private static final String ARG_PARAM4 = "content";
    private static final String ARG_PARAM5 = "seen";

    private String sender;
    private String recipient;
    private String topic;
    private String content;
    private boolean seen;

    private OnFragmentInteractionListener mListener;

    public EmailMessage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EmailMessage.
     */
    public static EmailMessage newInstance(String sender, String recipient, String topic, String content, boolean seen) {
        EmailMessage fragment = new EmailMessage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, sender);
        args.putString(ARG_PARAM2, recipient);
        args.putString(ARG_PARAM3, topic);
        args.putString(ARG_PARAM4, content);
        args.putString(ARG_PARAM5, seen + "");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sender = getArguments().getString(ARG_PARAM1);
            recipient = getArguments().getString(ARG_PARAM2);
            topic = getArguments().getString(ARG_PARAM3);
            content = getArguments().getString(ARG_PARAM4);
            seen = Boolean.parseBoolean(getArguments().getString(ARG_PARAM5));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_email_message, container, false);

        ((TextView)rootView.findViewById(R.id.from_field)).setText("From: " + sender);
        ((TextView)rootView.findViewById(R.id.to_field)).setText("To: " + recipient);
        ((TextView)rootView.findViewById(R.id.topic_field)).setText("Topic: " + topic);
        ((TextView)rootView.findViewById(R.id.content_field)).setText(content);
        ((RadioButton)rootView.findViewById(R.id.read_radio)).setChecked(seen);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
