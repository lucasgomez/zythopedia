/**
 * Copy paste thx to Sean McPherson at https://medium.com/@seanmcp/%EF%B8%8F-how-to-use-emojis-in-react-d23bbf608bf7
 * @param {[type]} props [description]
 */
const Emoji = props => (
    <span
        className="emoji"
        role="img"
        aria-label={props.label ? props.label : ""}
        aria-hidden={props.label ? "false" : "true"}
        title={props.label ? props.label : ""}
        onClick={props.onClick}
    >
        {props.symbol}
        {props.text}
    </span>

);

export default Emoji;
