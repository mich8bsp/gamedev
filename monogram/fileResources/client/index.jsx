//import axios from "axios";

class Board extends React.Component {

    state = {
        boardConfig: [],
        boardRows: 0,
        boardCols: 0
    };

    componentDidMount() {
        axios
          .get("/getPuzzle")
          .then(response => {
            console.log("got response", response.data)

            // create a new "State" object without mutating
            // the original State object.
            const newState = Object.assign({}, this.state, {
              boardConfig: response.data.board,
              boardRows: response.data.rows,
              boardCols: response.data.cols
            });

            // store the new state object in the component's state
            this.setState(newState);
          })
          .catch(error => console.log(error));
      }

    render() {
        return (<table><tbody>
        { this.state.boardConfig.map(x => (<tr>{x.map(y => (<td style={{backgroundColor: y.color}}> {y.text} </td>))}</tr>))}
        </tbody></table>);
    }
}

ReactDOM.render(
    <Board />,
    document.getElementById('root')
);