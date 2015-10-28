package com.dsmscavhunt;

/**
 * Created by Matthew on 10/10/15.
 *
 * Java Class created to store Scavenger Hunt item objects
 */
public class ScavItem {
    /* private member variables to store the
        auto-generated ID as an int
        the name of the location as a string
        the address of the location as a string
     */

    private int _id;
    private String _name;
    private String _address;
    private String _directions;
    private int _image; // images are stored as int values which will be resolved to Drawables

    // Constructors

    public ScavItem(String _name, String _address, String _directions, int _image) {
        this._name = _name;
        this._address = _address;
        this._directions = _directions;
        this._image = _image;
    }

    public ScavItem(int _id, String _name, String _address, String _directions, int _image) {
        this._id = _id;
        this._name = _name;
        this._address = _address;
        this._directions = _directions;
        this._image = _image;
    }

    public ScavItem() {
    }

    // Getters and Setters

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_address() {
        return _address;
    }

    public void set_address(String _address) {
        this._address = _address;
    }

    public String get_directions() { return _directions; }

    public void set_directions(String _directions) { this._directions = _directions; }

    public int get_image() { return _image; }

    public void set_image(int _image) { this._image = _image; }
}
