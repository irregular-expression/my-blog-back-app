package ru.irrexp.practicum.controller;

import ru.irrexp.practicum.controller.contract.CommentContract;
import ru.irrexp.practicum.controller.contract.ImageContract;
import ru.irrexp.practicum.controller.contract.PostContract;

public interface ApiController extends ImageContract, PostContract, CommentContract {

}
