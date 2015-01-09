<?php

/* Head ends here */
function nextMove($player,$board){
    $x = -1;
    $y = -1;
    for($i=0;$i<29;$i++)
    {
        for($j=0;$j<29;$j++)
        {
            if($board[($i*$j)%29][($i+$j)%29] == '-')
            {
                $x = ($i*$j)%29;
                $y = ($i+$j)%29;
            }
        }
    }
    if ($x == -1)
    {
       for($i=0;$i<29;$i++)
        {
            for($j=0;$j<29;$j++)
            {
                if($board[$i][$j] == '-')
                {
                    $x = $i;
                    $y = $j;
                }
            }
        } 
    }
    echo $x." ".$y;
}
/* Tail starts here */
$fp = fopen("php://stdin", "r");

fscanf($fp, "%s", $player);

$board = array();
for ($i=0; $i<29; $i++) {
    fscanf($fp, "%s", $board[$i]);
}

nextMove($player,$board);

?>