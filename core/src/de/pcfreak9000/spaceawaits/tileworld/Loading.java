package de.pcfreak9000.spaceawaits.tileworld;

public class Loading {
 
    //Set with loaded chunks
    //Set with updated chunks
    //Set with rendered chunks??? -> Server? Usefulness? Just loop through the loaded chunks and check if they need to be rendered somewhere?
    //Entitys in the Chunks?
    //Use a map instead with chunk coordinates? 
    //How to update the contents of the sets? -> PlayerMovementEvent? (oof because before collision... 
    //use event with result and query movement? quite oof actually) -> needs thoughts about events in general
    //Iterating through sets to find out about the individuals, meh -> additional Arrays for each step?
    
    //For networking, make World abstract or something? -> requestChunk
    
}
