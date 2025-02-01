package com.example.routes

import com.example.data.model.Note
import com.example.data.model.SimpleResponse
import com.example.data.model.User
import com.example.repository.Repo
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.response.*


const val NOTES = "$API_VERSION/notes"
const val CREATES_NOTES = "$NOTES/create"
const val UPDATES_NOTES = "$NOTES/update"
const val DELETE_NOTES = "$NOTES/delete"

fun Route.noteRoutes(
    db : Repo,
    hashFunction: (String) -> String
) {
    authenticate ("jwt"){

        post(CREATES_NOTES) {
            val note = try {
                call.receive<Note>()
            }catch (ex: Exception){
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing Fields"))
                return@post
            }

            try {
                val email = call.principal<User>()!!.email
                db.addNote(note, email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Note added successfully"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message?:"Some problem occurred"))
            }
        }

        get(NOTES) {
            try {
                val email = call.principal<User>()!!.email
                val notes = db.getAllNotes(email)
                call.respond(HttpStatusCode.OK, notes)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, emptyList<Note>())
            }
        }

        post(UPDATES_NOTES) {
            val note = try {
                call.receive<Note>()
            }catch (ex: Exception){
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing Fields"))
                return@post
            }

            try {
                val email = call.principal<User>()!!.email
                db.updateNote(note, email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Note updated successfully"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message?:"Some problem occurred"))
            }
        }

        delete (DELETE_NOTES){

            val noteId = try {
                call.request.queryParameters["id"]!!
            } catch (e: Exception){
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Query Parameter Id is missing"))
                return@delete
            }

            try {
                val email = call.principal<User>()!!.email
                db.deleteNote(noteId, email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Note deleted successfully"))
            }catch (e: Exception){
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message?:"Some problem occurred"))
            }
        }
    }
}