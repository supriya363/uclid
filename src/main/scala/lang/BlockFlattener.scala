/*
 * UCLID5 Verification and Synthesis Engine
 *
 * Copyright (c) 2017.
 * Sanjit A. Seshia, Rohit Sinha and Pramod Subramanyan.
 *
 * All Rights Reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 1. Redistributions of source code must retain the above copyright notice,
 *
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 *
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Authors: Pramod Subramanyan
 *
 * Flatten unnecessary BlockStmts.
 *
 */
package uclid
package lang

class BlockFlattenerPass extends RewritePass {
  override def rewriteBlock(blkStmt : BlockStmt, context : Scope) : Option[Statement] = {
    val stmtsP = blkStmt.stmts.flatMap {
      (st) => {
        st match {
          case BlockStmt(stmts) => stmts
          case _ => List(st)
        }
      }
    }
    stmtsP.size match {
      case 0 => Some(SkipStmt())
      case 1 => Some(stmtsP(0))
      case _ => Some(BlockStmt(stmtsP))
    }
  }
}

class BlockFlattener extends ASTRewriter("BlockFlattener", new BlockFlattenerPass())
{
  override def visit(module : Module, context : Scope) : Option[Module] = {
    var m : Module = module
    var modP : Option[Module] = None
    var done = false
    do {
      val modP1 = visitModule(m, context)
      done = (modP1 == modP)
      modP1 match {
        case None => done = true
        case Some(mod) => m = mod
      }
      modP = modP1
    } while(!done)
    modP
  }
}